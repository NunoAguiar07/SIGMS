import {useEffect, useState} from "react";
import {TeacherUser} from "../types/teacher/TeacherUser";
import {ParsedError} from "../types/errors/ParseErrorTypes";
import {fetchTeacherProfileById} from "../services/authorized/fetchTeacherProfileById";

export const useTeacherProfile = (teacherId: number) => {
    const [profile, setProfile] = useState<TeacherUser | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<ParsedError | null>(null);

    useEffect(() => {
        const fetchProfile = async () => {
            try {
                const response = await fetchTeacherProfileById(teacherId);
                setProfile(response);
            } catch (err) {
                console.error("Error:", err);
                setError(err as ParsedError);
            } finally {
                setLoading(false);
            }
        };

        fetchProfile();
    }, [teacherId]);

    return { profile, loading, error };
}


