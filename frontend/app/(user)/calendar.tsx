import React from "react";
import ErrorHandler from "../(public)/error";
import LoadingPresentation from "../../screens/auxScreens/LoadingScreen";
import {useSchedule} from "../../hooks/useSchedule";
import CalendarScreen from "../../screens/mainScreens/CalendarScreen";

const Calendar = () => {
    const { error, loading, onClickProfile, onClickRoom, getEventsForDay, getCurrentDay, selectedDay, setSelectedDay, daysOrder, navigateDay } = useSchedule();

    if (loading) return <LoadingPresentation />;
    if (error) return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;

    return (
        <CalendarScreen
            onClickProfile={onClickProfile}
            onClickRoom={onClickRoom}
            getEventsForDay={getEventsForDay}
            getCurrentDay={getCurrentDay}
            selectedDay={selectedDay}
            setSelectedDay={setSelectedDay}
            daysOrder={daysOrder}
            navigateDay={navigateDay}
        />
    );
};

export default Calendar;