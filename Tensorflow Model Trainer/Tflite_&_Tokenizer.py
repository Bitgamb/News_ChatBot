import pandas as pd
import numpy as np
import tensorflow as tf
from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences

# Load your dataset
df = pd.read_csv('english_news_dataset.csv')

# Separate text and labels
texts = df['text'].tolist()
labels = df['label'].tolist()

# Tokenize the texts
tokenizer = Tokenizer()
tokenizer.fit_on_texts(texts)

# Save the tokenizer to CSV
df_tokenizer = pd.DataFrame(list(tokenizer.word_index.items()), columns=['Word', 'Index'])
df_tokenizer.to_csv('tokenizer.csv', index=False)

# Prepare sequences for model training
sequences = tokenizer.texts_to_sequences(texts)
padded_sequences = pad_sequences(sequences, maxlen=500, padding='post', truncating='post')

# Convert labels to a NumPy array
labels = np.array(labels)

# Build and train a simple model
model = tf.keras.Sequential([
    tf.keras.layers.Embedding(input_dim=len(tokenizer.word_index) + 1, output_dim=32, input_length=500),
    tf.keras.layers.Flatten(),
    tf.keras.layers.Dense(1, activation='sigmoid')
])

model.compile(optimizer='adam', loss='binary_crossentropy', metrics=['accuracy'])
model.fit(padded_sequences, labels, epochs=10)

# Convert and save the model to TFLite format
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

with open('model.tflite', 'wb') as f:
    f.write(tflite_model)
