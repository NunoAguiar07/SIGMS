import {commonStyles} from "../css_styling/common/CommonProps";
import {Image} from "expo-image";

export const Logo = () => {
    return <Image source={require('../../assets/Logo.webp')} style={commonStyles.logo} />
}