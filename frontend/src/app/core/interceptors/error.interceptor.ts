import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { ApiError, ApiRequestError } from '../models/api-error.model';

function isApiError(payload: unknown): payload is ApiError {
  if (typeof payload !== 'object' || payload === null) {
    return false;
  }
  const candidate = payload as Partial<ApiError>;
  return typeof candidate.message === 'string' && typeof candidate.status === 'number';
}

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((httpError: HttpErrorResponse) => {
      if (httpError.status === 0) {
        return throwError(
          () =>
            new ApiRequestError(
              'Unable to reach the backend API. Please verify the Spring Boot server is running.',
              0
            )
        );
      }

      if (isApiError(httpError.error)) {
        const apiError = httpError.error;
        const fieldErrorMessages = Object.values(apiError.fieldErrors ?? {});
        const combinedMessage =
          fieldErrorMessages.length > 0
            ? `${apiError.message}: ${fieldErrorMessages.join('; ')}`
            : apiError.message;

        return throwError(
          () =>
            new ApiRequestError(
              combinedMessage,
              apiError.status,
              apiError.fieldErrors ?? {},
              apiError.path
            )
        );
      }

      const fallbackMessage =
        typeof httpError.error === 'string' && httpError.error.trim().length > 0
          ? httpError.error
          : httpError.message || 'An unexpected request error occurred.';

      return throwError(
        () => new ApiRequestError(fallbackMessage, httpError.status)
      );
    })
  );
};
