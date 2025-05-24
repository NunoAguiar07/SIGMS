import {ScrollView, Text, View} from "react-native";
import {styles} from "../../css_styling/calendar/Props";
import CalendarScreen from "../../screens/calendarScreen";
import {useEffect, useState} from "react";
import {ErrorInterface} from "../../interfaces/ErrorInterface";
import ErrorHandler from "../(public)/error";
import LoadingPresentation from "../../screens/Loading";
import {getSchedule} from "../../requests/authorized/CalendarPage";


const calendar = () => {
    const [schedule, setSchedule] = useState([]);
    const [error, setError] = useState<ErrorInterface | null>(null);

    useEffect(() => {
        // @ts-ignore
        const fetchData = getSchedule(setSchedule, setError);
        fetchData();
    }, []);

    if (error) {
        return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;
    }

    if (!schedule || schedule.length === 0) {
        return <LoadingPresentation />;
    }

    return <CalendarScreen schedule={schedule} />;
};

export default calendar;