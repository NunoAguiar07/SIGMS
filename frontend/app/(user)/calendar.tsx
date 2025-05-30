import CalendarScreen from "../../screens/calendarScreen";
import React, {useState} from "react";
import {ErrorInterface} from "../../interfaces/ErrorInterface";
import ErrorHandler from "../(public)/error";
import LoadingPresentation from "../../screens/LoadingScreen";
import {getSchedule} from "../../requests/authorized/CalendarPage";
import {useFocusEffect} from "expo-router";


const Calendar = () => {
    const [schedule, setSchedule] = useState([]);
    const [error, setError] = useState<ErrorInterface | null>(null);

    useFocusEffect(
        React.useCallback(() => {
            // @ts-ignore
            const fetchData = getSchedule(setSchedule, setError);
            fetchData();

        }, []) // Add dependencies if needed
    );

    if (error) {
        return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;
    }

    if (!schedule || schedule.length === 0) {
        return <LoadingPresentation />;
    }

    return <CalendarScreen schedule={schedule} />;
};

export default Calendar;