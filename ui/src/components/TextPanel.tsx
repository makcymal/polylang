import React from 'react';
import "./TextPanel.css"

type StringConsumer = (s: string) => void;

interface TextPanelProps {
    text: string;
    isEditable?: boolean;
    onTextEdit?: StringConsumer;
    title?: string;
    placeholder?: string;
    hint?: string;
}

export const TextPanel: React.FC<TextPanelProps> = ({
    text,
    isEditable,
    onTextEdit,
    title,
    placeholder,
    hint,
}: TextPanelProps) => {

    const onTextEditInternal = (evt: React.ChangeEvent<HTMLTextAreaElement>) => {
        if (onTextEdit != null) {
            onTextEdit(evt.target.value);
        }
    };

    return (
        <div className="panel">
            {title != null && <div className="panel-title disable-selection">{title}</div>}
            {
                isEditable ? (
                    <textarea
                        className="panel-text"
                        value={text}
                        readOnly={false}
                        onChange={onTextEditInternal}
                        placeholder={placeholder}
                    />
                ) : (
                    <textarea
                        className="panel-text"
                        value={text}
                        readOnly={true}
                        placeholder={placeholder}
                    />
                )
            }
            {hint != null && <div className="panel-hint disable-selection">{hint}</div>}
        </div>
    );

};

export default TextPanel;