import React from 'react'
import {HomeScreen} from "../../screens/mainScreens/HomeScreen";
import LoadingPresentation from "../../screens/auxScreens/LoadingScreen";
import ErrorHandler from "../(public)/error";
import {useLogout} from "../../hooks/useAuth";

const Home = () => {
    const { handleLogout, loadingLogout, errorLogout } = useLogout();

    if (loadingLogout) {
        return <LoadingPresentation />;
    }
    if (errorLogout) {
        return <ErrorHandler errorMessage={errorLogout.message} errorStatus={errorLogout.status} />;
    }

    return <HomeScreen onLogout={handleLogout} />;
};

export default Home;