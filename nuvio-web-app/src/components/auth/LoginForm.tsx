import React, { useState } from 'react';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { validateEmail, validatePassword } from '../../utils/validation';
import { LoginFormProps } from '@/types/login/LoginFormProps';

interface FormErrors {
  email?: string;
  password?: string;
  form?: string;
}

const LoginForm: React.FC<LoginFormProps> = ({ onSubmit, loading }) => {
  const [email, setEmail] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [errors, setErrors] = useState<FormErrors>({});

  const validateForm = (): boolean => {
    const newErrors: FormErrors = {};

    const emailValidation = validateEmail(email);
    if (!emailValidation.isValid) {
      newErrors.email = emailValidation.error || undefined;
    }

    const passwordValidation = validatePassword(password);
    if (!passwordValidation.isValid) {
      newErrors.password = passwordValidation.error || undefined;
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    const result = await onSubmit(email, password);
    
    if (!result.token) {
      setErrors({ form: 'Please check your credentials' });
    }
  };

  const clearError = (field: keyof FormErrors): void => {
    if (errors[field]) {
      setErrors({ ...errors, [field]: undefined });
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4 w-full">
      {errors.form && (
        <div className="bg-destructive/15 text-destructive text-sm p-3 rounded-md border border-destructive/50">
          {errors.form}
        </div>
      )}

      <div className="space-y-2">
        <Label htmlFor="email">Email</Label>
        <Input
          id="email"
          type="email"
          value={email}
          onChange={(e) => {
            setEmail(e.target.value);
            clearError('email');
            clearError('form');
          }}
          placeholder="you@example.com"
          disabled={loading}
          autoComplete="email"
          className={errors.email ? 'border-destructive' : ''}
        />
        {errors.email && (
          <p className="text-sm text-destructive">{errors.email}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="password">Password</Label>
        <Input
          id="password"
          type="password"
          value={password}
          onChange={(e) => {
            setPassword(e.target.value);
            clearError('password');
            clearError('form');
          }}
          placeholder="••••••••"
          disabled={loading}
          autoComplete="current-password"
          className={errors.password ? 'border-destructive' : ''}
        />
        {errors.password && (
          <p className="text-sm text-destructive">{errors.password}</p>
        )}
      </div>

      <Button 
        type="submit" 
        className="w-full" 
        loading={loading} 
        disabled={loading}
      >
        {loading ? 'Logging in...' : 'Login'}
      </Button>
    </form>
  );
};

export default LoginForm;