import {useFocusEffect, useRouter} from "expo-router";
import {useCallback, useEffect, useState} from "react";
import {ParsedError} from "../types/errors/ParseErrorTypes";
import {fetchSchedule} from "../services/authorized/FetchSchedule";
import {LectureWithTeacher} from "../types/LectureWithTeacher";
import AsyncStorage from "@react-native-async-storage/async-storage";
import {UserRole} from "../types/navBar/NavInterface";

export const useSchedule = () => {
    const [userRole, setUserRole] = useState<UserRole | null>(null);
    const [schedule, setSchedule] = useState<LectureWithTeacher[]>([]);
    const [error, setError] = useState<ParsedError | null>(null);
    const [loading, setLoading] = useState(true);
    const TIMING = 150000; // 2.5min
    const router = useRouter()

    useEffect(() => {
        const fetchUserRole = async () => {
            try {
                const role = await AsyncStorage.getItem('userRole');
                    setUserRole(role as UserRole);
            } catch (error) {
                console.error('Failed to fetch user role', error);
            }
        };
        fetchUserRole();
    }, []);

    useFocusEffect(
        useCallback(() => {
            const loadSchedule = async () => {
                if(userRole && (userRole == 'STUDENT' || userRole == 'TEACHER')){
                    try {
                        setLoading(true);
                        const data = await fetchSchedule();
                        setSchedule(data.lectures);
                    } catch (err) {
                        setError(err as ParsedError);
                    } finally {
                        setLoading(false);
                    }
                } else {
                    setLoading(false)
                }
            };
            loadSchedule();
            const interval = setInterval(() => {
                loadSchedule();
            }, TIMING);
            return () => clearInterval(interval);
        }, [userRole])
    );

    const onClickProfile =  (id :number) => {
        router.push(`/profile/` + encodeURIComponent(id));
    }
    const onClickRoom = (id: number) => {
        router.push(`/roomReports/` + encodeURIComponent(id));
    }

    const shouldShowCalendar = userRole === 'STUDENT' || userRole === 'TEACHER';

    return {shouldShowCalendar, schedule, error, loading, onClickProfile, onClickRoom};
};