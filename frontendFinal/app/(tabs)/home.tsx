
import {View, Text} from "react-native";
import React from 'react'
import {Image} from "expo-image";
// @ts-ignore


const home = () => {
    return (
        <View>
            <Image source={require("../../assets/Logo.webp")}></Image>
        </View>
    )
}

export default home