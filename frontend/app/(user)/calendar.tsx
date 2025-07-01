import React from "react";
import ErrorHandler from "../(public)/error";
import LoadingPresentation from "../../screens/auxScreens/LoadingScreen";
import {useSchedule} from "../../hooks/useSchedule";
import CalendarScreen from "../../screens/mainScreens/CalendarScreen";

const Calendar = () => {
    const { schedule, error, loading, onClickProfile, onClickRoom } = useSchedule();

    if (loading) return <LoadingPresentation />;
    if (error) return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;

    return <CalendarScreen schedule={schedule} onClickProfile={onClickProfile} onClickRoom={onClickRoom} />;
};

export default Calendar;