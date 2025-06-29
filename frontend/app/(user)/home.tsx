import React from 'react'
import {HomeScreen} from "../../screens/mainScreens/HomeScreen";
import LoadingPresentation from "../../screens/auxScreens/LoadingScreen";
import ErrorHandler from "../(public)/error";
import {useLogout} from "../../hooks/useAuth";
import {useProfile} from "../../hooks/useProfile";
import {useSchedule} from "../../hooks/useSchedule";
import {useNotifications} from "../../hooks/notifications/useNotifications";
import {BackgroundImage} from "../../screens/components/BackgroundImage";

const Home = () => {
    const { handleLogout, loadingLogout, errorLogout } = useLogout();
    const { profile, loading: loadingProfile, error: errorProfile } = useProfile();
    const { schedule, loading: loadingSchedule, error: errorSchedule } = useSchedule()
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
        <BackgroundImage>
            <HomeScreen onLogout={handleLogout} username={profile.username} schedule={schedule} notifications={notifications} clearNotification={clearNotification} />
        </BackgroundImage>
    );
};

export default Home;