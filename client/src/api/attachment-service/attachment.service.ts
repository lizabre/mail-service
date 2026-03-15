import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {AttachmentResponse} from './attachment.models';

/**
 * Service for managing mail attachments via the REST API.
 */
@Injectable({providedIn: 'root'})
export class AttachmentService {
  private readonly API_URL = 'http://localhost:8080/api/v1.0/mails';

  constructor(private http: HttpClient) {}

  /**
   * Retrieves the binary content of an attachment as an object URL.
   * @param mailId The ID of the mail containing the attachment.
   * @param attachmentId The ID of the attachment to retrieve.
   * @returns An observable emitting a blob object URL string.
   */
  getAttachmentContent(mailId: string, attachmentId: string): Observable<string> {
    return this.http.get(
      `${this.API_URL}/${mailId}/attachments/${attachmentId}/content`,
      {responseType: 'blob'}
    ).pipe(map(blob => URL.createObjectURL(blob)));
  }

  /**
   * Uploads a file as an attachment to a mail draft.
   * @param mailId The ID of the mail to attach the file to.
   * @param file The file to upload.
   * @returns An observable emitting the created {@link AttachmentResponse}.
   */
  uploadAttachment(mailId: string, file: File): Observable<AttachmentResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<AttachmentResponse>(
      `${this.API_URL}/${mailId}/attachments`,
      formData
    );
  }

  /**
   * Deletes an attachment from a mail draft.
   * @param mailId The ID of the mail.
   * @param attachmentId The ID of the attachment to delete.
   * @returns An observable that completes when the deletion is done.
   */
  deleteAttachment(mailId: string, attachmentId: string): Observable<void> {
    return this.http.delete<void>(
      `${this.API_URL}/${mailId}/attachments/${attachmentId}`
    );
  }
}
