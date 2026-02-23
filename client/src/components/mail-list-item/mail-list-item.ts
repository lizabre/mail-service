import {Component, Input} from '@angular/core';
import {MatListItem, MatListItemIcon, MatListItemLine, MatListItemTitle} from '@angular/material/list';
import {MatIcon} from '@angular/material/icon';
import {MatIconButton} from '@angular/material/button';

@Component({
  selector: 'app-mail-list-item',
  imports: [
    MatListItem,
    MatIcon,
    MatListItemIcon,
    MatListItemTitle,
    MatListItemLine,
    MatIconButton,
  ],
  templateUrl: './mail-list-item.html',
  styleUrl: './mail-list-item.css',
})
export class MailListItem {
  @Input({required:true}) id!: string;
  @Input({required:true}) subject!: string;
  @Input({required:true}) sender!: string;
  @Input({required:true}) content!: string;
  @Input({required:true}) isExternal!: boolean;
}
