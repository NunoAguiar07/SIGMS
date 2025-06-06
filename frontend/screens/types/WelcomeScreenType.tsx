import {WelcomeData} from "../../types/welcome/WelcomeInterfaces";


export interface WelcomeScreenType {
    welcome: WelcomeData;
    onMicrosoftPress: () => void;
    microsoftDisabled: boolean;
}