import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatListItem, MatListItemIcon, MatListItemLine, MatListItemMeta, MatListItemTitle, MatNavList} from '@angular/material/list';
import {MatIcon} from '@angular/material/icon';
import {MatIconButton} from '@angular/material/button';
import {Router} from '@angular/router';
import {MailStatus} from '../../api/mails-service/mails.models';

/**
 * Component representing a single mail item in a list.
 * Navigates to the mail detail or draft editor on click,
 * and emits a delete event when the delete button is pressed.
 */
@Component({
  selector: 'app-mail-list-item',
  imports: [
    MatListItem,
    MatIcon,
    MatListItemIcon,
    MatListItemTitle,
    MatListItemLine,
    MatIconButton,
    MatListItemMeta
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
  @Input({required: true}) status!: MailStatus;

  @Output() deleteClicked = new EventEmitter<string>();

  constructor(private router: Router) {}

  /**
   * Navigates to the draft editor for drafts, or the mail detail view for sent mails.
   */
  navigateToMail(): void {
    if (this.status === 'DRAFT') {
      this.router.navigate(['/new-mail'], {queryParams: {id: this.id}});
    } else {
      this.router.navigate(['/mail'], {queryParams: {id: this.id}});
    }
  }

  /**
   * Emits the delete event and stops click propagation to prevent navigation.
   * @param event The mouse click event.
   */
  onDelete(event: MouseEvent): void {
    event.stopPropagation();
    this.deleteClicked.emit(this.id);
  }
}
