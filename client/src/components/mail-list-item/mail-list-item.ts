import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatListItem, MatListItemIcon, MatListItemLine, MatListItemMeta, MatListItemTitle, MatNavList} from '@angular/material/list';
import {MatIcon} from '@angular/material/icon';
import {MatIconButton} from '@angular/material/button';
import {Router} from '@angular/router';

@Component({
  selector: 'app-mail-list-item',
  imports: [
    MatListItem,
    MatIcon,
    MatListItemIcon,
    MatListItemTitle,
    MatListItemLine,
    MatIconButton,
    MatListItemMeta,
    MatNavList
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

  @Output() deleteClicked = new EventEmitter<string>();

  constructor(private router: Router) {}

  navigateToMail(): void {
    this.router.navigate(['/mail'], {queryParams: {id: this.id}});
  }

  onDelete(event: MouseEvent): void {
    event.stopPropagation();
    this.deleteClicked.emit(this.id);
  }
}
