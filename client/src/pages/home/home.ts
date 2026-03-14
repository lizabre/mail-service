import {Component} from '@angular/core';
import {MatTab, MatTabGroup} from '@angular/material/tabs';
import {MailList} from '../../components/mail-list/mail-list';

@Component({
  selector: 'app-home',
  imports: [
    MatTabGroup,
    MatTab,
    MailList
  ],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home{
}
