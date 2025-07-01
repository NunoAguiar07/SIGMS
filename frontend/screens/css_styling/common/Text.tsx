import {StyleSheet} from "react-native";

const TextWeights = StyleSheet.create({
    black: {
        fontFamily: 'RobotoCondensed-Black',
    },
    bold: {
        fontFamily: 'RobotoCondensed-Bold',
    },
    regular: {
        fontFamily: 'RobotoCondensed-Regular',
    },
    light: {
        fontFamily: 'RobotoCondensed-Light',
    }
})

const TextSizes = StyleSheet.create({
    h1: {
        fontSize: 36,
        textAlign: "center"
    },
    h2: {
        fontSize: 24,
        textAlign: "center"
    }
})

const TextColors = StyleSheet.create({
    primary: {
        color: '#671b22'
    }
})

export const TextStyles = StyleSheet.create({
    ...TextColors,
    ...TextSizes,
    ...TextWeights
})