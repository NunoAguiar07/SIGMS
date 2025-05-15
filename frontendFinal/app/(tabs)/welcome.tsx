import {useEffect, useState} from "react";
import {WelcomeInterface} from "../../interfaces/WelcomeInterface";
import {ErrorInterface} from "../../interfaces/ErrorInterface";
import {WelcomeScreen} from "../../css_mainscreens/WelcomeScreen";
import {GetData} from "../../requests/WelcomeRequest";
import ErrorHandler from "./error";
import LoadingPresentation from "../../css_mainscreens/Loading";

const welcome = () => {
        const [welcome, setHome] = useState<WelcomeInterface | null>(null)
        const [error, setError] = useState<ErrorInterface | null>(null)

        //Use effect that will do when we fetch the information about the home page.
        useEffect(() => {
            const fetchData = GetData(setHome, setError)
            fetchData()
        }, [])

        //If an error happens we will show the error page.
        if (error) return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />

        //If the home is still loading we will represent the loading screen.
        if (!welcome) return <LoadingPresentation />

        //Return of the home screen.
        return <WelcomeScreen welcome={welcome}/>
}

export default welcome