export interface Report {
  id: number;
  reporterId: number;
  reporterUsername: string;
  postId: number;
  postTitle: string;
  message: string;
  resolved: boolean;
  createdAt: string;
  adminNotes?: string;
}

export interface CreateReportDTO {
  postId: number;
  message: string;
}

export interface UpdateReportDTO {
  resolved: boolean;
  adminNotes?: string;
}
