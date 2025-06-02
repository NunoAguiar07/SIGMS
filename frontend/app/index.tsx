import {Redirect} from "expo-router";
import LoadingPresentation from "../screens/auxScreens/LoadingScreen";
import {useFontsLoad} from "../hooks/useFontsLoad";


const Index = () => {
    const fontsLoaded = useFontsLoad();
    if (!fontsLoaded) {
        return <LoadingPresentation />;
    }
    return <Redirect href={"/welcome"} />
};


export default Index;