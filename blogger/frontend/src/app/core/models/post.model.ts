
export interface Post {
  id: number;
  title: string;
  content: string;
  author: PostAuthor;
  likeCount: number;
  isHidden: boolean; // important
  isLiked?: boolean; 
  tags: string[];
  commentCount?: number;
  createdAt: string;
  updatedAt: string;
  comments?: Comment[];
}

export interface PostAuthor {
  id: number;
  username: string;
  fullName?: string;
  avatar?: string;
}

export interface CreatePost {
  title: string;
  content: string;
  tags?: string[];
}

export interface Comment {
  id: number;
  author: string;
  content: string;
  likeCount: number;
  createdAt: string;
  updatedAt: string;
  responses?: Response[];
}

export interface Response {
  id: number;
  author: string;
  content: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateComment {
  content: string;
}
