package service.sms;

/**
 * 응답 Status
 * HTTP Status	Desc
 * 202	Accept (요청 완료)
 * 400	Bad Request
 * 401	Unauthorized
 * 403	Forbidden
 * 404	Not Found
 * 429	Too Many Requests
 * 500	Internal Server Error
 */
public class HttpResponseCode {

    public static final int HTTP_ACCEPTED = 202;

    public static final int HTTP_BAD_REQUEST = 400;

    public static final int HTTP_UNAUTHORIZED = 401;

    public static final int HTTP_FORBIDDEN = 403;

    public static final int HTTP_NOT_FOUND = 404;

    public static final int HTTP_TOO_MANY_REQUESTS = 500;

    public static final int HTTP_INTERNAL_ERROR = 500;

}
