import {DimensionValue, StyleSheet, ViewStyle} from "react-native";

const containerShapes = StyleSheet.create({
    roundedContainer: {
        borderRadius: 30,
        padding: 5
    },
    border: {
        borderWidth: 5,
    },
    fullHeight: {
        height: '100%'
    }
})

const containerBackgroundColors = StyleSheet.create({
    backgroundPrimary: {
        backgroundColor: '#ead6bd'
    },
    backgroundSecondary: {
        backgroundColor: '#bf9e9b'
    },
    backgroundOnPrimary: {
        backgroundColor: '#f8f0e6'
    }
})

const containerBorderColors = StyleSheet.create({
    borderPrimary: {
        borderColor: '#671b22',
    }
})

export const getColumnStyle = (widthPercent: number, gap: number, justify: ViewStyle['justifyContent'] = 'center') => {
    return {
        ...ContainerStyles.columnBase,
        width: `${widthPercent}%` as DimensionValue,
        gap: gap,
        justifyContent: justify
    };
}

export const ContainerStyles = StyleSheet.create({
    container: {
        flexDirection: 'row',
        width: '100%',
        height: '100%',
        padding: 10,
    },
    columnBase: {
        flexDirection: 'column',
        padding: 10,
        alignItems: 'center',
    },
    ...containerShapes,
    ...containerBackgroundColors,
    ...containerBorderColors
});
