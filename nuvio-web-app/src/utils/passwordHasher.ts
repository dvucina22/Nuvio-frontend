export class PasswordHasher {
  static async transformPassword(password: string, email: string): Promise<string> {
    const normalizedEmail = email.trim().toLowerCase();
    
    const salt = await this.sha256(normalizedEmail);
    
    const iterations = 120_000;
    const keyLengthBits = 256;
    
    const passwordBuffer = new TextEncoder().encode(password);
    
    const keyMaterial = await crypto.subtle.importKey(
      'raw',
      passwordBuffer,
      'PBKDF2',
      false,
      ['deriveBits']
    );
    
    const derivedBits = await crypto.subtle.deriveBits(
      {
        name: 'PBKDF2',
        salt: salt,
        iterations: iterations,
        hash: 'SHA-256'
      },
      keyMaterial,
      keyLengthBits
    );
    
    return this.arrayBufferToBase64(derivedBits);
  }
  
  private static async sha256(text: string): Promise<ArrayBuffer> {
    const buffer = new TextEncoder().encode(text);
    return await crypto.subtle.digest('SHA-256', buffer);
  }
  
  private static arrayBufferToBase64(buffer: ArrayBuffer): string {
    const bytes = new Uint8Array(buffer);
    let binary = '';
    for (let i = 0; i < bytes.byteLength; i++) {
      binary += String.fromCharCode(bytes[i]);
    }
    return btoa(binary);
  }
}