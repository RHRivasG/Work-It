import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Training } from '../../models/training';
import { TrainingService } from '../../services/training.service';

@Component({
  selector: 'wi-new-training',
  templateUrl: './new-training.component.html',
  styleUrls: ['./new-training.component.scss']
})
export class NewTrainingComponent implements OnInit {
  trainingForm: FormGroup
  preferences: string[]
  loading = false
  training?: Training

  constructor(route: ActivatedRoute, formBuilder: FormBuilder, private service: TrainingService, private router: Router) {
    this.training = route.snapshot.data.training
    this.preferences = route.snapshot.data.preferences
    if (this.training) {
      this.trainingForm = formBuilder.group({
        name: [this.training.name, Validators.required],
        tags: [this.training.categories],
        description: [this.training.description, Validators.required],
        video: [{ size: this.training.video.length, ext: this.training.video.ext, name: this.training.video.name }]
      })
    } else {
      this.trainingForm = formBuilder.group({
        name: ['', Validators.required],
        tags: [''],
        description: ['', Validators.required],
        video: [{}]
      })
    }
  }

  ngOnInit(): void {
  }

  submit() {
    this.loading = true
    if (this.training) {
      this.training.name = this.trainingForm.get('name')?.value
      this.training.categories = this.trainingForm.get('tags')?.value
      this.training.description = this.trainingForm.get('description')?.value
      this.training.video.ext = this.trainingForm.get('video')?.value.ext
      this.training.video.name = this.trainingForm.get('video')?.value.name
      this.training.video.length = this.trainingForm.get('video')?.value.size
      this.training.video.video = this.trainingForm.get('video')?.value.value?.replace("data:video/mp4;base64,", "")
      console.log(this.training)
      this.service.update(this.training).subscribe(() => this.loading = false)
    } else {
      const video = this.trainingForm.get('video')?.value
      this.service.create(
        this.trainingForm.get('name')?.value,
        this.trainingForm.get('description')?.value,
        this.trainingForm.get('tags')?.value,
        {
          video: video.value.replace("data:video/mp4;base64,", ""),
          ext: video.ext,
          length: video.size,
          name: video.name
        }
      ).subscribe(() => {
        this.router.navigate(['/fitness/trainer/trainings'])
        this.loading = false
      })
    }
  }
}
