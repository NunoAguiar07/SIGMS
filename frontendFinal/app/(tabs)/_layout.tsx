import { Tabs } from "expo-router"

export default () => {
    return (
        <Tabs
            screenOptions={{
                headerShown: false,}}>
            <Tabs.Screen name="home"/>
            <Tabs.Screen name="welcome"/>
            <Tabs.Screen name="error"/>
            <Tabs.Screen name="about"/>
            <Tabs.Screen name="privacy"/>
            <Tabs.Screen name="faq"/>
            <Tabs.Screen name="(auth)"/>
        </Tabs>
    )
}

