import { Tabs } from "expo-router"

export default () => {
    return (
        <Tabs
        screenOptions={{
            headerShown: false,}}>
            <Tabs.Screen name="profile" />
        </Tabs>
    )
}