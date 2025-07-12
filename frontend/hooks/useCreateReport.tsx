import {useEffect, useRef, useState} from "react";
import {RoomInterface} from "../types/RoomInterface";
import {useDebounce} from "use-debounce";
import {fetchRooms} from "../services/authorized/FetchRooms";
import {CreateIssueReportRequest} from "../services/authorized/CreateIssueReport";
import {ParsedError} from "../types/errors/ParseErrorTypes";
import {fetchIssueReportsByRoom} from "../services/authorized/FetchIssueReportsByRoom";
import {useAlert} from "./notifications/useAlert";


export const useCreateReport = () => {
    const [rooms, setRooms] = useState<RoomInterface[]>([]);
    const [selectedRoom, setSelectedRoom] = useState<RoomInterface | null>(null);
    const [reportText, setReportText] = useState('');
    const [searchQuery, setSearchQuery] = useState('');
    const [debouncedSearchQuery] = useDebounce(searchQuery, 500);
    const [error, setError] = useState<ParsedError | null>(null);
    const [issues, setIssues] = useState<any[]>([]);

    const issueCache = useRef<{ [roomId: number]: any[] }>({});

    const showAlert = useAlert()

    useEffect(() => {
        if (debouncedSearchQuery.trim().length > 0) {
            fetchRooms(debouncedSearchQuery)
                .then((data) => {
                    setRooms(data);
                }).catch(err => {
                    setError(err as ParsedError);
                    setRooms([]);
                    setSelectedRoom(null);
            })
        } else {
            setRooms([]);
            setSelectedRoom(null);
            setError(null);
        }
    }, [debouncedSearchQuery]);

    useEffect(() => {
        if (selectedRoom) {
            getIssuesForRoom(selectedRoom.id);
        } else {
            setIssues([]);
        }
    }, [selectedRoom]);

    const handleRoomSelect = (room: RoomInterface) => {
        setSelectedRoom(room);
        setReportText('');
    };

    const handleSubmitReport = async (roomId: number, description: string) => {
        try {
            const success = await CreateIssueReportRequest(roomId, description);
            if (success) {
                showAlert('Success', 'Issue report created successfully');
                setSelectedRoom(null);
                setReportText('');
                delete issueCache.current[roomId];
                await getIssuesForRoom(roomId);
            } else {
                showAlert('Error', 'Failed to create issue report');
            }
        } catch (err) {
            setError(err as ParsedError);
        }
    };

    const getIssuesForRoom = async (roomId: number) => {
        if (issueCache.current[roomId]) {
            setIssues(issueCache.current[roomId]);
            return;
        }

        try {
            const fetchedIssues = await fetchIssueReportsByRoom(roomId);
            issueCache.current[roomId] = fetchedIssues;
            setIssues(fetchedIssues);
        } catch (err) {
            setError(error as ParsedError);
        }
    };



    return {
        rooms,
        issues,
        selectedRoom,
        reportText,
        searchQuery,
        error,
        setSearchQuery,
        setReportText,
        handleRoomSelect,
        handleSubmitReport,
        getIssuesForRoom
    };
};