import {
  Body,
  Controller,
  Get,
  Param,
  Post,
  UseGuards,
  Request,
  UnauthorizedException,
  Put,
  Delete,
} from '@nestjs/common';
import { JwtAuthGuard } from 'src/auth/jwt-auth.guard';
import { RoutineDto } from '../../../application/routine.dto';
import { RoutineService } from '../../../application/routine.service';
import { CreateRoutine } from '../../../application/commands/create-routine.command';
import { UpdateRoutine } from '../../../application/commands/update-routine.command';
import { DeleteRoutine } from '../../../application/commands/delete-routine.command';

class RoutineCreationForm {
  name: string;
  description: string;
  trainings: string[];
}

class RoutineUpdatingForm {
  name: string;
  description: string;
  trainings: string[];
}

@Controller('routines')
export class RoutineController {
  constructor(private readonly _routineService: RoutineService) {}

  @UseGuards(JwtAuthGuard)
  @Get()
  async getRoutines(@Request() req: any): Promise<RoutineDto[]> {
    const user = req.user;
    if (!user.roles.includes('participant')) {
      throw new UnauthorizedException();
    }
    return await this._routineService.getAll(user.userId);
  }

  @UseGuards(JwtAuthGuard)
  @Get(':id')
  async getRoutine(
    @Param('id') id: string,
    @Request() req: any,
  ): Promise<RoutineDto> {
    const user = req.user;
    if (!user.roles.includes('participant')) {
      throw new UnauthorizedException();
    }
    return await this._routineService.get(
      new Uint8Array(Buffer.from(id, 'hex')),
    );
  }

  @UseGuards(JwtAuthGuard)
  @Post()
  async create(
    @Request() req: any,
    @Body() routine: RoutineCreationForm,
  ): Promise<void> {
    const user = req.user;
    if (!user.roles.includes('participant')) {
      throw new UnauthorizedException();
    }
    const command: CreateRoutine = new CreateRoutine(
      routine.name,
      routine.description,
      user.userId,
      routine.trainings.map(
        (training) => new Uint8Array(Buffer.from(training, 'hex')),
      ),
    );
    command.execute(this._routineService);
  }

  @Put(':id')
  async update(
    @Param('id') id: string,
    @Request() req: any,
    @Body() routine: RoutineUpdatingForm,
  ): Promise<void> {
    const user = req.user;
    if (!user.roles.includes('participant')) {
      throw new UnauthorizedException();
    }
    const command: UpdateRoutine = new UpdateRoutine(
      new Uint8Array(Buffer.from(id, 'hex')),
      routine.name,
      routine.description,
      user.userId,
      routine.trainings.map(
        (training) => new Uint8Array(Buffer.from(training, 'hex')),
      ),
    );
    command.execute(this._routineService);
  }
  @Delete(':id')
  async delete(@Param('id') id: string, @Request() req: any): Promise<void> {
    const user = req.user;
    if (!user.roles.includes('participant')) {
      throw new UnauthorizedException();
    }
    const command: DeleteRoutine = new DeleteRoutine(
      new Uint8Array(Buffer.from(id, 'hex')),
    );
    command.execute(this._routineService);
  }
}
