package s188219_jsonparser;

/**
 *
 * @author S188219
 */
public class InvalidJSONException extends Exception {


		public InvalidJSONException()
		{
		}

		public InvalidJSONException(String message)
		{
			super(message);
		}

		public InvalidJSONException(Throwable cause)
		{
			super(cause);
		}

		public InvalidJSONException(String message, Throwable cause)
		{
			super(message, cause);
		}

		public InvalidJSONException(String message, Throwable cause, 
                                           boolean enableSuppression, boolean writableStackTrace)
		{
			super(message, cause, enableSuppression, writableStackTrace);
		}

}