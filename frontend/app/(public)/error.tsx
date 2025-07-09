import {ErrorScreen} from "../../screens/auxScreens/ErrorScreen";
import {goBack} from "expo-router/build/global-state/routing";
import {ErrorHandlerType} from "../../screens/types/ErrorScreenType";



/**
 * Const "ErrorHandler" responsible to show the error page.
 * @param errorStatus represents the code of the error that happened.
 * @param errorMessage represents the message of the error.
 */
const ErrorHandler = ({errorStatus, errorMessage} : ErrorHandlerType) => {
    return (
        <ErrorScreen
            errorStatus={errorStatus}
            errorMessage={errorMessage.split('-').join(' ')}
            goBack={goBack}
        />
    )
}

export default ErrorHandler