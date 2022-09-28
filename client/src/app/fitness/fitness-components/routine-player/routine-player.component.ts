import { HttpClient } from '@angular/common/http';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { FullRoutine } from '../../models/routine';
import { Summary } from '../../models/summary';
import { Training } from '../../models/training';

@Component({
  selector: 'wi-routine-player',
  templateUrl: './routine-player.component.html',
  styleUrls: ['./routine-player.component.scss'],
})
export class RoutinePlayerComponent implements OnInit {
  trainings: Training[] = [];
  routine!: FullRoutine;
  summary!: Summary;
  indexActualTraining: number = 0;
  @ViewChild('video')
  video!: ElementRef;
  sec: number = 0;
  min: number = 0;
  hrs: number = 0;
  t: any;

  constructor(private route: ActivatedRoute, private http: HttpClient) {
    route.data.subscribe((data) => {
      this.routine = data.routine;
      this.trainings = this.routine.trainings;
      this.summary = data.summary;
    });
    console.log(this.summary);
  }

  ngOnInit(): void {
    console.log(this.trainings);
  }

  ngAfterViewInit(): void {
    this.timer();
    this.setVideo();
  }

  timer() {
    window.setInterval(() => {
      this.tick();
    }, 1000);
  }

  tick = () => {
    this.sec++;
    if (this.sec >= 60) {
      this.sec = 0;
      this.min++;
      if (this.min >= 60) {
        this.min = 0;
        this.hrs++;
      }
    }
  };

  nextVideo() {
    if (this.indexActualTraining < this.trainings.length - 1) {
      this.indexActualTraining += 1;
      this.setVideo();
    } else {
      this.showSummary();
    }
  }

  previousVideo() {
    if (this.indexActualTraining > 0) {
      this.indexActualTraining -= 1;
      this.setVideo();
    }
  }

  showSummary() {
    let msg =
      'Completed the routine in ' +
      this.hrs +
      ':' +
      this.min +
      ':' +
      this.sec +
      ' hours!\n' +
      'Current maxtime: ' +
      this.summary.maxtime +
      '\n' +
      'Current mintime: ' +
      this.summary.mintime +
      '\n';

    window.alert(msg);
    this.http
      .put(
        environment.summaryApiUrl + '/routines/' + this.routine.id + '/summary',
        {
          time: `${this.hrs}h${this.min}m${this.sec}s`,
        },
        { responseType: 'text' }
      )
      .subscribe(() => {});
  }

  setVideo() {
    const video = <HTMLVideoElement>this.video.nativeElement;

    this.http
      .get(
        environment.trainingApiUrl +
          '/' +
          this.trainings[this.indexActualTraining].id +
          '/video',
        { responseType: 'text' }
      )
      .pipe(catchError((_) => of()))
      .subscribe(async (file) => {
        const url = 'data:video/mp4;base64,' + file,
          r = await fetch(url),
          blob = await r.blob();

        console.log(blob);

        video.src = URL.createObjectURL(blob);
      });
  }
}
