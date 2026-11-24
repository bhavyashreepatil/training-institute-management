export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  fieldErrors: Record<string, string>;
}

export class ApiRequestError extends Error {
  readonly status: number;
  readonly fieldErrors: Record<string, string>;
  readonly path: string;

  constructor(
    message: string,
    status: number,
    fieldErrors: Record<string, string> = {},
    path = ''
  ) {
    super(message);
    this.name = 'ApiRequestError';
    this.status = status;
    this.fieldErrors = fieldErrors;
    this.path = path;
  }
}
