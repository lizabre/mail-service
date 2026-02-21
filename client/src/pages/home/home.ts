import {Component} from '@angular/core';
import {MatTab, MatTabGroup} from '@angular/material/tabs';
import {MatListItem, MatListItemIcon, MatListItemLine, MatListItemTitle, MatNavList} from '@angular/material/list';
import {MatIcon} from '@angular/material/icon';
import {MatPaginator} from '@angular/material/paginator';
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
