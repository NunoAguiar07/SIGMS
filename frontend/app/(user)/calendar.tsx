import React from "react";
import ErrorHandler from "../(public)/error";
import LoadingPresentation from "../../screens/auxScreens/LoadingScreen";
import {useSchedule} from "../../hooks/useSchedule";
import CalendarScreen from "../../screens/CalendarScreen";

const Calendar = () => {
    const { schedule, error, loading } = useSchedule();

    if (loading) return <LoadingPresentation />;
    if (error) return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;

    return <CalendarScreen schedule={schedule} />;
};

export default Calendar;