import {useStudyRooms} from "../../hooks/useStudyRooms";
import LoadingPresentation from "../../screens/auxScreens/LoadingScreen";
import ErrorHandler from "../(public)/error";
import React from "react";
import StudyRoomScreen from "../../screens/mainScreens/StudyRoomScreen";
import {BackgroundImage} from "../../screens/components/BackgroundImage";

const StudyRooms = () => {
    const { studyRooms, setUpdate, error, loading } = useStudyRooms();
    if (loading) return <LoadingPresentation />;
    if (error) return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;
    return(
        <BackgroundImage>
            <StudyRoomScreen studyRooms={studyRooms} forceUpdate={setUpdate}/>
        </BackgroundImage>
    )
}

export default StudyRooms;