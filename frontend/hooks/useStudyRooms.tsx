import {useCallback, useState} from "react";
import {StudyRoomCapacity} from "../types/StudyRoomCapacity";
import {ParsedError} from "../types/errors/ParseErrorTypes";
import {fetchStudyRoomCapacity} from "../services/authorized/FetchStudyRoomCapacity";
import {updateStudyRoomCapacity} from "../services/authorized/UpdateStudyRoomCapacity";
import {useFocusEffect} from "expo-router";

export const useStudyRooms = () => {
    const [studyRooms, setStudyRooms] = useState<StudyRoomCapacity[]>([]);
    const [updateStudyRooms, setUpdate] = useState<boolean>(true);
    const [error, setError] = useState<ParsedError | null>(null);
    const [loading, setLoading] = useState<boolean>(true);

    const loadStudyRooms = async () => {
        try {
            const data = await fetchStudyRoomCapacity()
            setStudyRooms(data)
        } catch (e){
            setError(e as ParsedError)
        } finally {
            setLoading(false)
        }
    }

    const forceUpdate = async () => {
        try {
           await updateStudyRoomCapacity()
        } catch (err) {
            setError(err as ParsedError)
        }
    }

    useFocusEffect(useCallback(() => {
            forceUpdate()
            loadStudyRooms()
        }, [updateStudyRooms])
    )

    return { studyRooms, setUpdate, error, loading };
}