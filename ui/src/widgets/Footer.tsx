import '@/widgets/Footer.css';

function Footer() {
    return (
        <footer className="footer">
            <div className="footer-signature">
                polylang by makcymal ·{' '}
                <a href="https://github.com/makcymal/polylang" target="_blank" rel="noreferrer">
                    GitHub
                </a>
            </div>
            <div>
                <a href="mailto:makcymal@yandex.ru" className="footer-email">
                    makcymal@yandex.ru
                </a>
            </div>
        </footer>
    );
}

export default Footer;
