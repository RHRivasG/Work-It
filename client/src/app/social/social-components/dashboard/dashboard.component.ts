import { Portal, TemplatePortal } from '@angular/cdk/portal';
import { HttpClient } from '@angular/common/http';
import { AfterViewInit, Component, Inject, OnDestroy, OnInit, TemplateRef, ViewChild, ViewContainerRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin, of, Subscription } from 'rxjs';
import { catchError, map, mapTo, switchMap, tap } from 'rxjs/operators';
import { webSocket } from 'rxjs/webSocket';
import { GlobalSearch, WI_GLOBAL_SEARCH } from 'src/app/services/global-search';
import { environment } from 'src/environments/environment';
import { Participant } from '../../models/participant';
import { RequestStatus } from '../../models/request-status';
import { Trainer } from '../../models/trainer';

type TabMap = {
  [key: string]: Portal<unknown>
}

@Component({
  selector: 'wi-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent implements OnInit, OnDestroy, AfterViewInit {
  tab!: string
  private participantsDataSource: Participant[]
  private trainersDataSource: Trainer[]
  participants: Participant[] = []
  trainers: Trainer[] = []
  @ViewChild("requests") requestsTab!: TemplateRef<unknown>
  @ViewChild("participants") participantsTab!: TemplateRef<unknown>
  @ViewChild("trainers") trainersTab!: TemplateRef<unknown>
  tabMap: TabMap = {}
  participantsStreamSubscription: Subscription
  trainersStreamSubscription: Subscription
  searchSubscription: Subscription

  get searchedParticipants() {
    return this.participants
  }

  get searchedTrainers() {
    return this.trainers
  }

  get validSourceLength() {
    return this.searchService.result.pipe(
      map(l => l.length > 0)
    )
  }

  get pendingRequests() {
    return this.participants
    .map(({ requestStatus, name, id }) => ({ requestStatus, name, id }))
    .filter(request => request.requestStatus == RequestStatus.Pending)
  }

  idOf(index: number, entity: { id: string }) {
    return entity.id
  }

  constructor(
    private viewContainerRef: ViewContainerRef,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    @Inject(WI_GLOBAL_SEARCH) private searchService: GlobalSearch<Participant | Trainer>
  ) {
    this.participantsDataSource = activatedRoute.snapshot.data.participants
    this.trainersDataSource = activatedRoute.snapshot.data.trainers
    activatedRoute.queryParams.subscribe(param => {
      if (["requests", "participants", "trainers"].includes(param.tab)) {
        this.tab = param.tab
        if (this.tab == "trainers")
          this.searchService.dataSource = this.trainersDataSource
        else
          this.searchService.dataSource = this.participantsDataSource
      } else {
        this.router.navigate(['.'], { relativeTo: activatedRoute, queryParams: { tab: 'requests', secret: activatedRoute.snapshot.queryParams.secret } })
      }
    })
    this.searchService.extractor = (p: Participant | Trainer) => p.name
    this.searchSubscription = this.searchService.result.subscribe(res => {
      if (res.length == 0) {
        this.participants = []
        this.trainers = []
      } else if ("requestStatus" in res[0]) {
        this.participants = res as Participant[]
      } else {
        this.trainers = res as Trainer[]
      }
    })
    this.participantsStreamSubscription = webSocket<Participant[]>(environment.socialStreamingApiUrl + "/participants/stream")
    .pipe(
      switchMap(list => list.length == 0 ? of([]) : forkJoin(
          list.map(p => this.http.get(environment.socialApiUrl + "/participants/" + p.id + "/request")
          .pipe(
            mapTo({ ...p, requestStatus: RequestStatus.Pending }),
            catchError(_ => of({ ...p, requestStatus: RequestStatus.NotSent }))
          ))
        )
      )
    )
    .subscribe(list => {
      console.log(list)
      this.participantsDataSource = list
      if (this.tab != "trainers")
        this.searchService.dataSource = this.participantsDataSource
    })
    this.trainersStreamSubscription = webSocket<Trainer[]>(environment.socialStreamingApiUrl + "/trainers/stream").subscribe(list => {
      console.log(list)
      this.trainersDataSource = list
      if (this.tab == "trainers")
        this.searchService.dataSource = this.trainersDataSource
    })
  }
  ngOnDestroy(): void {
    this.searchSubscription.unsubscribe()
    this.participantsStreamSubscription.unsubscribe()
    this.trainersStreamSubscription.unsubscribe()
  }

  ngAfterViewInit(): void {
    this.tabMap.requests = new TemplatePortal(this.requestsTab, this.viewContainerRef)
    this.tabMap.participants = new TemplatePortal(this.participantsTab, this.viewContainerRef)
    this.tabMap.trainers = new TemplatePortal(this.trainersTab, this.viewContainerRef)
  }

  ngOnInit(): void {
  }

  selectTab(tab: string) {
    this.router.navigate(['.'], { relativeTo: this.activatedRoute, queryParams: { tab, secret: this.activatedRoute.snapshot.queryParams.secret } })
  }

  acceptRequest(participantId: string) {
    this.actionOnRequest(participantId, "accept")
  }

  denyRequest(participantId: string) {
    this.actionOnRequest(participantId, "reject")
  }

  private actionOnRequest(id: string, ep: string) {
    this.http.post(
      environment.socialApiUrl + "/participants/" + id + "/request/" + ep,
      {},
      { responseType: 'text' }
    )
    .subscribe(() => {
      const participant = this.participants.find(p => p.id == id)
    })
  }
}
