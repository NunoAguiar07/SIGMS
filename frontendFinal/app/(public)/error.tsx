import {ErrorScreen} from "../../screens/ErrorScreen";
import {goBack} from "expo-router/build/global-state/routing";


/**
 * Const "ErrorHandler" responsible to show the error page.
 * @param errorStatus represents the code of the error that happened.
 * @param errorMessage represents the message of the error.
 */
// @ts-ignore
const ErrorHandler = ({errorStatus, errorMessage}) => {
    return <ErrorScreen errorStatus={errorStatus} errorMessage={errorMessage.split('-').join(' ')} goBack={goBack}/>
}

export default ErrorHandler