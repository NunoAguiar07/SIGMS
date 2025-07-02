import {useEffect, useState} from "react";
import {ParsedError} from "../types/errors/ParseErrorTypes";
import {fetchIssueReportsByRoom} from "../services/authorized/FetchIssueReportsByRoom";
import {IssueReportInterface} from "../types/IssueReportInterface";
import {RoomInterface} from "../types/RoomInterface";

export const useRoomReports = (roomId: number) => {
    const [reports, setReports] = useState<IssueReportInterface[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<ParsedError | null>(null);
    const [room , setRoom] = useState<RoomInterface | null>(null);


    const loadReports = async () => {
        try {
            const data = await fetchIssueReportsByRoom(roomId);
            setReports(data);
            if (data.length > 0) {
                setRoom(data[0].room);
            }
        } catch (err) {
            setError(err as ParsedError);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (roomId) {
            loadReports();
        }
    }, [roomId]);

    return { reports, room, loading, error };
};