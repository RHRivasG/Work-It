import { HttpClient } from '@angular/common/http';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { FullRoutine } from '../../models/routine';
import { Training } from '../../models/training';

@Component({
  selector: 'wi-routine-player',
  templateUrl: './routine-player.component.html',
  styleUrls: ['./routine-player.component.scss'],
})
export class RoutinePlayerComponent implements OnInit {
  trainings: Training[] = [];
  routine!: FullRoutine;
  indexActualTraining: number = 0;
  @ViewChild('video')
  video!: ElementRef;

  constructor(private route: ActivatedRoute, private http: HttpClient) {
    route.data.subscribe((data) => {
      this.routine = data.routine;
      this.trainings = this.routine.trainings;
    });
  }

  ngOnInit(): void {
    console.log(this.trainings);
  }

  ngAfterViewInit(): void {
    this.setVideo();
  }

  setVideo() {
    const video = <HTMLVideoElement>this.video.nativeElement;

    this.http
      .get(
        environment.fitnessApiUrl +
          '/trainings/' +
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
