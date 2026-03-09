import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {AttachmentResponse} from './attachment.models';

@Injectable({providedIn: 'root'})
export class AttachmentService {
  private readonly API_URL = 'http://localhost:8080/api/v1.0/mails';

  constructor(private http: HttpClient) {
  }

  getAttachmentContent(mailId: string, attachmentId: string): Observable<string> {
    return this.http.get(
      `${this.API_URL}/${mailId}/attachments/${attachmentId}/content`,
      {responseType: 'blob'}
    ).pipe(map(blob => URL.createObjectURL(blob)));
  }

  uploadAttachment(mailId: string, file: File): Observable<AttachmentResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<AttachmentResponse>(
      `${this.API_URL}/${mailId}/attachments`,
      formData
    );
  }

  deleteAttachment(mailId: string, attachmentId: string): Observable<void> {
    return this.http.delete<void>(
      `${this.API_URL}/${mailId}/attachments/${attachmentId}`
    );
  }
}
