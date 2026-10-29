export interface PaginatedResponse<T> {
    content: T[]; // The list of items (e.g., products)
    pageNumber: number;
    pageSize: number;
    totalElements: number;
    totalPages: number;
  }