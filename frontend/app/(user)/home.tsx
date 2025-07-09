import React from 'react'
import {HomeScreen} from "../../screens/mainScreens/HomeScreen";
import LoadingPresentation from "../../screens/auxScreens/LoadingScreen";
import ErrorHandler from "../(public)/error";
import {useLogout} from "../../hooks/useAuth";
import {useProfile} from "../../hooks/useProfile";
import {useSchedule} from "../../hooks/useSchedule";
import {useNotifications} from "../../hooks/notifications/useNotifications";

const Home = () => {
    const { handleLogout, loadingLogout, errorLogout } = useLogout();
    const { profile, loading: loadingProfile, error: errorProfile } = useProfile(undefined);
    const { shouldShowCalendar, schedule, loading: loadingSchedule, error: errorSchedule, onClickProfile, onClickRoom } = useSchedule()
    const { notifications, clearNotification } = useNotifications()
    const errors = [errorLogout, errorProfile, errorSchedule];
    const firstError = errors.find(err => err != null);

    if (loadingLogout || loadingSchedule || loadingProfile || !profile) {
        return <LoadingPresentation/>;
    }
    if (firstError) {
        return <ErrorHandler errorMessage={firstError.message} errorStatus={firstError.status} />;
    }
    return (
        <HomeScreen shouldShowCalendar={shouldShowCalendar} onLogout={handleLogout} username={profile.username} schedule={schedule} notifications={notifications} clearNotification={clearNotification} onClickRoom={onClickRoom} onClickProfile={onClickProfile} />
    );
};

export default Home;