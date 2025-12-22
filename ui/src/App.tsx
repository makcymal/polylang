import {useState, useEffect} from 'react'
import {type Theme, THEME, DATA_THEME, LIGHT, DARK} from '@/types/Theme.ts'
import '@/App.css'
import Gym from '@/widgets/Gym.tsx'
import Header from "@/widgets/Header.tsx";
import Footer from "@/widgets/Footer.tsx";

function App() {

    const [theme, setTheme] = useState<Theme>(() => {
        const saved = localStorage.getItem(THEME)
        return (saved === DARK ? DARK : LIGHT) as Theme
    })

    useEffect(() => {
        document.documentElement.setAttribute(DATA_THEME, theme)
        localStorage.setItem(THEME, theme)
    }, [theme])

    const switchTheme = () => {
        setTheme((prev) => (prev === LIGHT ? DARK : LIGHT))
    }

    return (
        <div className="app">
            <Header theme={theme} switchTheme={switchTheme}/>
            <Gym theme={theme}/>
            <Footer/>
        </div>
    )

}

export default App
