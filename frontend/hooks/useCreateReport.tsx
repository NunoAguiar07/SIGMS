import {useEffect, useRef, useState} from "react";
import {RoomInterface} from "../types/RoomInterface";
import {useDebounce} from "use-debounce";
import {fetchRooms} from "../services/authorized/FetchRooms";
import {CreateIssueReportRequest} from "../services/authorized/CreateIssueReport";
import {Alert} from "react-native";
import {ParsedError} from "../types/errors/ParseErrorTypes";
import {fetchIssueReportsByRoom} from "../services/authorized/FetchIssueReportsByRoom";


export const useCreateReport = () => {
    const [rooms, setRooms] = useState<RoomInterface[]>([]);
    const [selectedRoom, setSelectedRoom] = useState<RoomInterface | null>(null);
    const [reportText, setReportText] = useState('');
    const [searchQuery, setSearchQuery] = useState('');
    const [debouncedSearchQuery] = useDebounce(searchQuery, 500);
    const [error, setError] = useState<ParsedError | null>(null);
    const [issues, setIssues] = useState<any[]>([]);

    const issueCache = useRef<{ [roomId: number]: any[] }>({});

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
        const submit = CreateIssueReportRequest(roomId, description);
        const success = await submit;

        if (success) {
            Alert.alert('Success', 'Report submitted successfully!');
            setSelectedRoom(null);
            setReportText('');
            delete issueCache.current[roomId];
            await getIssuesForRoom(roomId);
        } else {
            Alert.alert('Error', 'Failed to submit the report. Please try again.');
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
            Alert.alert('Error', 'Failed to fetch reports for the selected room.');
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