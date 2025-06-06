import {useFocusEffect} from "expo-router";
import {useCallback, useState} from "react";
import {ParsedError} from "../types/errors/ParseErrorTypes";
import {fetchSchedule} from "../services/authorized/fetchSchedule";
import {Lecture} from "../types/calendar/Lecture";

export const useSchedule = () => {
    const [schedule, setSchedule] = useState<Lecture[]>([]);
    const [error, setError] = useState<ParsedError | null>(null);
    const [loading, setLoading] = useState(true);
    const TIMING = 150000; // 2.5min

    useFocusEffect(
        useCallback(() => {
            const loadSchedule = async () => {
                try {
                    const data = await fetchSchedule();
                    setSchedule(data);
                } catch (err) {
                    setError(err as ParsedError);
                } finally {
                    setLoading(false);
                }
            };
            loadSchedule();
            const interval = setInterval(() => {
                loadSchedule();
            }, TIMING);
            return () => clearInterval(interval);
        }, [])
    );

    return { schedule, error, loading};
};