import { Component } from '@angular/core';
import {MatIconButton, MatMiniFabButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {RouterLink} from '@angular/router';
import {Attachment} from '../../components/attachment/attachment';

@Component({
  selector: 'app-mail',
  imports: [
    MatIcon,
    MatMiniFabButton,
    RouterLink,
    MatIconButton,
    Attachment
  ],
  templateUrl: './mail.html',
  styleUrl: './mail.css',
})
export class Mail {

}
