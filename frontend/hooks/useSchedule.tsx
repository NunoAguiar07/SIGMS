import {useFocusEffect, useRouter} from "expo-router";
import {useCallback, useState} from "react";
import {ParsedError} from "../types/errors/ParseErrorTypes";
import {fetchSchedule} from "../services/authorized/fetchSchedule";
import {LectureWithTeacher} from "../types/LectureWithTeacher";

export const useSchedule = () => {
    const [schedule, setSchedule] = useState<LectureWithTeacher[]>([]);
    const [error, setError] = useState<ParsedError | null>(null);
    const [loading, setLoading] = useState(true);
    const TIMING = 150000; // 2.5min
    const router = useRouter()

    useFocusEffect(
        useCallback(() => {
            const loadSchedule = async () => {
                try {
                    const data = await fetchSchedule();
                    setSchedule(data.lectures);
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

    const onClickProfile =  (id :number) => {
        router.push(`/teacher/` + encodeURIComponent(id));
    }
    const onClickRoom = (id: number) => {
        router.push(`/roomReports/` + encodeURIComponent(id));
    }

    return { schedule, error, loading, onClickProfile, onClickRoom};
};