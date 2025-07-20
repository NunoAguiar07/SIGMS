import ErrorHandler from "../../(public)/error";
import LoadingPresentation from "../../../screens/auxScreens/LoadingScreen";
import {useLocalSearchParams} from "expo-router";
import {useRoomReports} from "../../../hooks/useRoomReports";
import {RoomReportsScreen} from "../../../screens/mainScreens/RoomReportScreen";

const RoomId = () => {
    const {roomId} = useLocalSearchParams();
    const { reports, room, loading, error } = useRoomReports(Number(roomId));

    if (error) {
        return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;
    }
    if (loading) {
        return <LoadingPresentation />;
    }

    return <RoomReportsScreen reports={reports} room={room}  />;
};

export default RoomId;