import '@/components/Button.css';

const Button = () => {
    return (
        <div className="component-container">
            <h1>Two-Button Component</h1>
            <p className="subtitle">Hover over each button to see the clockwise border animation</p>

            <div className="two-button-block">
                <button className="button button-left">
                    <div className="border-animation"></div>
                    <div className="button-content">
                        <div className="icon">
                            <i className="fas fa-compass"></i>
                        </div>
                        <span className="button-text">Compass</span>
                    </div>
                </button>

                <button className="button button-right">
                    <div className="border-animation"></div>
                    <div className="button-content">
                        <div className="icon">
                            <i className="fas fa-microphone"></i>
                        </div>
                        <span className="button-text">00:00</span>
                    </div>
                </button>
            </div>

            <div className="instructions">
                <h3>Component Features:</h3>
                <ul>
                    <li>Rectangle block with rounded corners divided vertically into two buttons</li>
                    <li>Left button: Compass icon with "Compass" text (simulating compass.png)</li>
                    <li>Right button: Microphone icon with "00:00" text (simulating microphone.png and timer)</li>
                    <li>All content is centered vertically and horizontally</li>
                    <li>On hover: Each button changes border color with clockwise animation</li>
                    <li>Additional hover effects: Background color change and icon scaling</li>
                </ul>
            </div>

            <p className="footer-note">
                Note: Using Font Awesome icons to simulate compass.png and microphone.png images
            </p>
        </div>
    );
};

export default Button;
