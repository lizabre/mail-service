import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {CreateMailRequest, MailFolder, MailResponse, UpdateMailRequest} from './mails.models';

/**
 * Service for interacting with the mail REST API.
 * Handles all CRUD operations and mail sending.
 */
@Injectable({providedIn: 'root'})
export class MailService {
  private readonly API_URL = 'http://localhost:8080/api/v1.0/mails';

  constructor(private http: HttpClient) {}

  /**
   * Fetches all mails in the specified folder.
   * @param folder The folder to fetch mails from.
   * @returns An observable emitting a list of {@link MailResponse}.
   */
  getMails(folder: MailFolder): Observable<MailResponse[]> {
    const params = new HttpParams().set('folder', folder);
    return this.http.get<MailResponse[]>(this.API_URL, {params});
  }

  /**
   * Fetches a single mail by ID.
   * @param mailId The ID of the mail to fetch.
   * @returns An observable emitting the {@link MailResponse}.
   */
  getMailById(mailId: string): Observable<MailResponse> {
    return this.http.get<MailResponse>(`${this.API_URL}/${mailId}`);
  }

  /**
   * Creates a new mail draft.
   * @param request The mail creation payload.
   * @returns An observable emitting the created {@link MailResponse}.
   */
  createMail(request: CreateMailRequest): Observable<MailResponse> {
    return this.http.post<MailResponse>(this.API_URL, request);
  }

  /**
   * Updates an existing mail draft.
   * @param mailId The ID of the mail to update.
   * @param request The updated mail payload.
   * @returns An observable emitting the updated {@link MailResponse}.
   */
  updateMail(mailId: string, request: UpdateMailRequest): Observable<MailResponse> {
    return this.http.put<MailResponse>(`${this.API_URL}/${mailId}`, request);
  }

  /**
   * Permanently deletes a mail and its attachments.
   * @param mailId The ID of the mail to delete.
   * @returns An observable that completes when the deletion is done.
   */
  deleteMail(mailId: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${mailId}`);
  }

  /**
   * Sends a mail draft.
   * @param mailId The ID of the draft to send.
   * @returns An observable emitting the updated {@link MailResponse}.
   */
  sendMail(mailId: string): Observable<MailResponse> {
    return this.http.post<MailResponse>(`${this.API_URL}/${mailId}/send`, {});
  }
}
