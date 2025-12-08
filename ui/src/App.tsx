import './App.css'

const steps = [
    'Вам будет предложен текст на русском языке',
    'Начните переводить его вслух',
    'ИИ проанализирует ваш перевод и даст подсказки для улучшения',
]

function App() {
    return (
        <div className="page">
            <main className="content">
                <header className="hero">
                    <h1>Говорите и думайте на английском свободно</h1>
                    <p className="lead">
                    Живой тренажёр для беглой речи без скучных правил
                    </p>
                    <button className="cta" type="button">Начать</button>
                </header>

                <section className="section">
                    <h2>Как это работает</h2>
                    <div className="grid">
                        {steps.map((step, index) => (
                            <div key={step} className="card">
                                <div className="step-number">{index + 1}</div>
                                <p>{step}</p>
                            </div>
                        ))}
                    </div>
                </section>
            </main>

            <footer className="footer">
                <div className="signature">
                    polylang by makcymal ·{' '}
                    <a href="https://github.com/makcymal/polylang" target="_blank" rel="noreferrer">
                        GitHub
                    </a>
                </div>
                <div>
                    <a href="mailto:makcymal@yandex.ru" className="email">makcymal@yandex.ru</a>
                </div>
            </footer>
        </div>
    )
}

export default App
