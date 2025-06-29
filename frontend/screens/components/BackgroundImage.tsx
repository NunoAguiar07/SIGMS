import {ImageBackground} from "expo-image";
import {ReactNode} from "react";
import {View} from "react-native";

export const BackgroundImage = ({children} :{children: ReactNode}) => {
    return (<ImageBackground style={{flex: 1, width: '100%', height: '100%',position: 'absolute', // Add this for root level
        top: 0,
        left: 0,
        right: 0,
        bottom: 0}}
                             imageStyle={{resizeMode: "cover"}}
                            source={require('../../assets/WebsiteBackground.png')}>
        <View style={{flex: 1}}>
            {children}
        </View>
    </ImageBackground>)
}